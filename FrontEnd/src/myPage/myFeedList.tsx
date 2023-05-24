import { faHeart } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React from "react";
import styled from "styled-components";
import useAsync, { getTest } from "../customhook/useAsync";

const MainContainer = styled.div`
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;

  width: 100%;
  justify-content: center;
`;

const CodyContainer = styled.div`
  text-align: center;

  margin: 2rem;
  padding: 2rem;

  .img {
    max-width: 300px;
    max-height: 300px;
    border-radius: 0.2rem;
    // box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.5);
  }

  div > span {
    margin: 1rem;
  }
`;
function MyFeedList() {
  const [state, fetchData] = useAsync(() => getTest("feed"), true);
  const { data } = state;

  const getLike = () => {
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
        <>
          {imageUrls.map((data: any) => (
            <CodyContainer key={data.userid}>
              <img className="img" src={data.imgsrc}></img>
            </CodyContainer>
          ))}
        </>
      );
    }
  };

  return <MainContainer>{getLike()}</MainContainer>;
}

export default MyFeedList;
