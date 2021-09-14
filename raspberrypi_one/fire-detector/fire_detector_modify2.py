import os
import cv2
import timeit
import numpy as np
import tensorflow as tf
from imutils.video import FPS
import imutils
import time
import RPi.GPIO as GPIO
import urllib.request
import urlopen
import json

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

GPIO.setup(26, GPIO.IN) #mq-5
GPIO.setup(16, GPIO.IN) #mq-2 

camera = cv2.VideoCapture(0)

# Loads label file, strips off carriage return
label_lines = [line.rstrip() for line
               in tf.gfile.GFile('./output_labels.txt')]

def grabVideoFeed():
    grabbed, frame = camera.read()
    return frame if grabbed else None

def initialSetup():
    os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
    start_time = timeit.default_timer()

    # This takes 2-5 seconds to run
    # Unpersists graph from file
    with tf.gfile.FastGFile('./output_graph.pb', 'rb') as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())
        tf.import_graph_def(graph_def, name='')

def sendNotification(token, channel,message):
    data = {
      "body" : message,
      "message_type" : "text/plain:"
    }
    req = urllib.request.Request('http://api.pushetta.com/api/pushes/{0}/'.format(channel))

    req.add_header('Content-Type', 'application/json')
    req.add_header('Authorization', 'Token {0}'.format(token))

    response = urllib.request.urlopen(req, json.dumps(data).encode('utf-8'))



initialSetup()
with tf.Session() as sess:
    start_time = timeit.default_timer()

    # Feed the image_data as input to the graph and get first prediction
    softmax_tensor = sess.graph.get_tensor_by_name('final_result:0')

    

    fps = FPS().start()

    while True:
        frame = grabVideoFeed()
        i=GPIO.input(26) # 5
        k=GPIO.input(16) # 2

        if frame is None:
            raise SystemError('Issue grabbing the frame')

        frame = cv2.resize(frame, (299, 299), interpolation=cv2.INTER_CUBIC)

        cv2.imshow('Main', frame)

        # adhere to TS graph input structure
        numpy_frame = np.asarray(frame)
        numpy_frame = cv2.normalize(numpy_frame.astype('float'), None, -0.5, .5, cv2.NORM_MINMAX)
        numpy_final = np.expand_dims(numpy_frame, axis=0)

        start_time = timeit.default_timer()

        # This takes 2-5 seconds as well
        predictions = sess.run(softmax_tensor, {'Mul:0': numpy_final})

        

        start_time = timeit.default_timer()

        # Sort to show labels of first prediction in order of confidence
        top_k = predictions[0].argsort()[-len(predictions[0]):][::-1]

        

        for node_id in top_k:
            human_string = label_lines[node_id]
            score = predictions[0][node_id]
            print('%s (score = %f)' % (human_string, score))
            if  human_string == "fire" and score > 0.5:
                if i==0 and k==0:
                     print('image detect',i)
                     sendNotification("095eacb1d4076fb252f3e4a838fcb22b7ed81a30","Fire_detcetion","image  Detected!!!!")
                elif  i==1 and k==1:
                     print('image and Fire sensor detect',i)
                     sendNotification("095eacb1d4076fb252f3e4a838fcb22b7ed81a30","Fire_detcetion","image Fire_sensor Detected!!!!")
                elif  i==0 and k==1:
                     print('iamge and gas detect',i)
                     sendNotification("095eacb1d4076fb252f3e4a838fcb22b7ed81a30","Fire_detcetion","image Gas  Detected!!!!")
                elif  i==1 and k==1:
                     print('iamge and Gas , Fire sensor detect',i)
                     sendNotification("095eacb1d4076fb252f3e4a838fcb22b7ed81a30","Fire_detcetion","image Gas  Fire_sensor Detected!!!!! ")
            elif human_string == "fire" and score<0.5:
                if  i==1 and k==0:
                     print('Fire sensor detect',i)
                     sendNotification("095eacb1d4076fb252f3e4a838fcb22b7ed81a30","Fire_detcetion","Only Fire_sensor Detected!!!!")
                elif  i==0 and k==1:
                     print('gas detect',i)
                     sendNotification("095eacb1d4076fb252f3e4a838fcb22b7ed81a30","Fire_detcetion","Only Gas  Detected!!!!")
                elif  i==1 and k==1:
                     print('Gas , Fire sensor detect',i)
                     sendNotification("095eacb1d4076fb252f3e4a838fcb22b7ed81a30","Fire_detcetion","Only Gas  Fire_sensor Detected!!!!! ")
  

        print ('********* Session Ended *********')

        key = cv2.waitKey(1) & 0xFF

        # if the `q` key was pressed, break from the loop
        if key == ord("q"):
                break
        fps.update()

             
fps.stop()
camera.release()
cv2.destroyAllWindows()
