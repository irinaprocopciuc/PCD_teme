from google.cloud import storage
from flask import Flask, request, render_template, Response, make_response
import logging
import os
#os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="C:/Users/iprocopc/OneDrive - Centric/Desktop/fac/Master-anul 2/SEM 2/PCD/serviceKey3.json"

app = Flask(__name__)

storage_client = storage.Client()

PREFIX = "thumbnail"

bucket_name = "tema3_img_upload"

#bucket = storage_client.create_bucket(bucket_name)

#print("Bucket {} created.".format(bucket.name))

@app.route('/')
def index():
    return render_template('form.html')

@app.route('/upload', methods=['POST'])
def upload():
    uploaded_file = request.files.get('file')

    if not uploaded_file:
        return 'No file uploaded.', 400

    gcs = storage.Client()

    bucket = gcs.get_bucket(bucket_name)

    blob = bucket.blob(uploaded_file.filename)

    blob.upload_from_string(
        uploaded_file.read(),
        content_type=uploaded_file.content_type
    )

    return 'Successfully uploaded to: ' + blob.public_url

@app.route('/buckets', methods=["GET", "POST"])
def buckets():
    if request.method not in ["GET", "POST"]:
        return method_not_allowed(request.method, request.path, ["GET", "POST"])

    resp = Response("")

    if request.method == "GET":

        buckets = storage_client.list_buckets()
        bucket = storage_client.get_bucket(bucket_name)
        return bucket.id

@app.errorhandler(500)
def server_error(e):
    logging.exception('An error occurred during a request.')
    return """
    An internal error occurred: <pre>{}</pre>
    See logs for full stacktrace.
    """.format(e), 500

if __name__ == "__main__":
    app.run(host='localhost', port=8080)
