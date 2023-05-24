import React from "react";
import styled from "styled-components";
import ImgUpload from "../imgUpload/imgUpload";
import Dialog from "../components/dialog";
import ImageSlider from "../components/imgSlider";

interface ClothType {
  [key: string]: string[];
}

interface Props {
  category: string;
  clothData: ClothType;
}

const ClothBox = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;

  max-width: 50%;
  max-heigth: 70%;
  overflow: scroll;

  margin: 1rem auto;
  padding: 1rem;
  border: 1px solid #c8c8c8;
  border-radius: 0.2rem;

  box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.5);
`;

function ClothData({ category, clothData }: Props) {
  const renderData = clothData[category.toLowerCase()];
  return (
    <ClothBox>
      <h2>{category}</h2>
      {renderData ? (
        <ImageSlider images={renderData}></ImageSlider>
      ) : (
        <div>카테고리에 등록된 이미지가 없습니다.</div>
      )}
      <div>
        <Dialog text="추가" title="새로운 옷을 업로드하세요">
          <ImgUpload category={category}></ImgUpload>
        </Dialog>
      </div>
    </ClothBox>
  );
}

export default ClothData;
