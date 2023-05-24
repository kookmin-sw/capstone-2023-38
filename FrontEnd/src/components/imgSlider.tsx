import React, { useEffect, useState } from "react";
import styled from "styled-components";
import useAsync, { deleteImg } from "../customhook/useAsync";
import Button from "./button";

interface imgList {
  id: number;
  imgsrc: string;
}

interface Props {
  images: any;
}
const ImgBox = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const ImgContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;

  margin: 2rem;
  padding: 2rem;

  .img {
    margin: 0 2rem 0 2rem;
    width: 250px;
    height: 250px;
    margin-top: 2rem;
    cursor: pointer;
  }
`;

const ImageSlider = ({ images }: Props) => {
  const [imageList, setImageList] = useState<imgList[]>(images);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [state, fetchData] = useAsync(
    () => deleteImg(images[currentIndex].imgsrc),
    false
  );
  const { loading, data, error } = state;

  useEffect(() => {
    setImageList(images); // images가 변경될 때마다 imageList를 업데이트
  }, [images]);

  const goToPrevImage = () => {
    setCurrentIndex(
      currentIndex === 0 ? imageList.length - 1 : currentIndex - 1
    );
  };

  const goToNextImage = () => {
    setCurrentIndex(
      currentIndex === imageList.length - 1 ? 0 : currentIndex + 1
    );
  };

  const handleDeleteImage = () => {
    // 이미지 삭제 로직 구현
    // images 배열에서 해당 이미지를 제거하는 등의 동작을 수행
    fetchData();
    console.log("이미지 삭제:", imageList[currentIndex]);
    if (loading) return <div>loading...</div>;
    if (error) return <div>error message {error.message}</div>;
    if (!data) return <div>저장된 옷이 없습니다</div>;

    setImageList(
      imageList.filter((img) => img.imgsrc !== imageList[currentIndex].imgsrc)
    );
  };

  return (
    <ImgBox>
      <ImgContainer>
        <Button onClick={goToPrevImage}>&lt;</Button>
        <img
          className="img"
          src={imageList[currentIndex].imgsrc}
          alt="slider"
        />
        <Button onClick={goToNextImage}>&gt;</Button>
      </ImgContainer>
      <Button fontcolor="red" onClick={handleDeleteImage}>
        삭제
      </Button>
    </ImgBox>
  );
};

export default ImageSlider;
