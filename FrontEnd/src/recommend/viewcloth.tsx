import React, { useEffect, useState } from "react";
import styled from "styled-components";
import Button from "../components/button";

interface imgList {
  id: number;
  imgsrc: string;
  path: string;
}

interface Props {
  data: any;
  state: imgList[];
  category: string;
  onSelectListChange: (selectlist: imgList[]) => void; // 콜백 함수 타입 정의
}

const Container = styled.div`
  display: flex;
  flex-direction: column;
  text-align: center;
  align-items: center;
`;
const ImgContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;

  margin: 1rem;
  padding: 1rem;

  .img {
    width: 200px;
    height: 200px;
    cursor: pointer;
  }
`;

function ViewCloth({ data, onSelectListChange, state, category }: Props) {
  const [selectlist, setSelectList] = useState<imgList[]>([]);

  const [imglist, setImageList] = useState<imgList[]>(data);
  const [idx, setIdx] = useState(0);

  useEffect(() => {
    setImageList(data);
    onSelectListChange(selectlist); // images가 변경될 때마다 imageList를 업데이트
  }, [data]);

  const prevImg = () => {
    setIdx(idx === 0 ? imglist.length - 1 : idx - 1);
  };
  const nextImg = () => {
    setIdx(idx === imglist.length - 1 ? 0 : idx + 1);
  };

  const handleSelect = (image: imgList) => {
    // console.log(image);
    let templist = [...selectlist];

    //중복이미지 체크
    for (let i = 0; i < templist.length; i++) {
      if (templist[i].imgsrc === image.imgsrc) {
        return alert("중복된 이미지 입니다.");
      }
    }
    //이미지 추가로직 구현
    templist.push({
      id: templist.length + 1,
      imgsrc: image.imgsrc,
      path: image.path,
    });

    setSelectList(templist);
    onSelectListChange(templist);
  };

  return (
    <>
      <Container>
        <ImgContainer>
          <Button fontcolor="skyblue" onClick={prevImg}>
            &lt;
          </Button>
          <img
            className="img"
            src={imglist[idx].imgsrc}
            onClick={() => handleSelect(imglist[idx])}
          ></img>
          <Button onClick={nextImg} fontcolor="skyblue">
            &gt;
          </Button>
        </ImgContainer>
      </Container>
    </>
  );
}
export default ViewCloth;
