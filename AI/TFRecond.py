import tensorflow as tf
import os
from PIL import Image
import json

# 이미지 파일과 해당 이미지에 대한 레이블 정보
image_dir = '/home/park/capstone/polyvore/model/train/img/'
label_file = '/home/park/capstone/polyvore/model/train/label/musinsa.json'

# TFRecord 저장 경로
output_file = '/home/park/capstone/polyvore/data/tf_records.tfrecord'

# 이미지 파일 확장자
image_extension = '.jpg'


def create_tf_example(image_path, label):
    with tf.io.gfile.GFile(image_path, 'rb') as fid:
        encoded_image = fid.read()
    feature_dict = {
        'image/encoded': tf.train.Feature(bytes_list=tf.train.BytesList(value=[encoded_image])),
        'image/label': tf.train.Feature(int64_list=tf.train.Int64List(value=[label]))
    }
    example = tf.train.Example(features=tf.train.Features(feature=feature_dict))
    return example


# JSON 파일에서 이미지에 대한 레이블 정보를 읽어옴
with open(label_file, 'r') as f:
    label_data = json.load(f)

# Check if label_data is a dictionary
if not isinstance(label_data, dict):
    raise ValueError("label_data must be a dictionary")

# TFRecord 작성기 생성
writer = tf.io.TFRecordWriter(output_file)

# 이미지 디렉토리에서 이미지 파일을 하나씩 읽어서 TFRecord로 저장
for filename, label in label_data.items():
    if filename.endswith(image_extension):
        image_path = os.path.join(image_dir, filename)
        tf_example = create_tf_example(image_path, label)
        writer.write(tf_example.SerializeToString())

# TFRecord 작성기 닫기
writer.close()
