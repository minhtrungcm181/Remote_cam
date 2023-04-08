import cv2
from flask import Flask, Response

app = Flask(__name__)

camera = cv2.VideoCapture(0) # 0 indicates the default camera

def generate_frames():
    while True:
        # Capture frame-by-frame
        success, frame = camera.read()
        # cv2.imshow('Frame', frame)
        if not success:
            break
        else:
            # Encode the frame in JPEG format
            ret, buffer = cv2.imencode('.jpg', frame)
            frame = buffer.tobytes()
            # Yield the frame in byte format
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')

@app.route('/')
def index():
    return "Camera Streaming App"

@app.route('/video_feed')
def video_feed():
    return Response(generate_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
