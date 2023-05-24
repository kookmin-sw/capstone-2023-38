import React, { useState } from "react";
import styled from "styled-components";
import MainBox from "../components/mainBox";
import useAsync, { getTest, getUsers } from "../customhook/useAsync";
import MyFeedList from "./myFeedList";
import MyLikeList from "./myLikeList";

const MainContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;

  span {
    font-family: "NanumSquareRound";
    font-style: normal;
    margin-top: 2rem;
    font-size: 1.5rem;
  }
`;
const WishContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  margin: 1rem;

  img {
    margin: 2rem;
    width: 300px;
    height: 300px;
    border-radius: 0.2rem;
    // box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.5);
  }

  .likebox {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
`;

const LikeImgBox = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
`;

function MyWishList() {
  const [state, fetchData] = useAsync(() => getTest("wish"), true);
  const [state3, fetchData3] = useAsync(() => getTest("like"), true);
  // const [state, fetchData] = useAsync(()=> getUsers("kookmin123"), true);
  const { loading, error, data } = state;

  const getData = () => {
    if (!data) return <div>loading..</div>;
    if (data) {
      const check = () => {
        try {
          return data.images;
        } catch (e: any) {
          console.log(e);
        }
      };

      const uidata = check();
      if (!uidata) console.log("failed load Data");
      // uidata를 배열로 변환
      const imageUrls = Array.isArray(uidata) ? uidata : [uidata];

      return (
        <WishContainer>
          {imageUrls.map((image) => (
            <img key={image.number} src={image.imgsrc} className="img" />
          ))}
        </WishContainer>
      );
    }
  };

  // console.log(getData());

  return (
    <MainContainer>
      <span>위시리시트 목록</span>
      {getData()}
      <span>좋아요 목록</span>
      <MyLikeList></MyLikeList>
      <span>내 피드 목록</span>
      <MyFeedList></MyFeedList>
    </MainContainer>
  );
}

export default MyWishList;
