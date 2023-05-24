import { faHeart } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React from "react";
import styled from "styled-components";
import MainBox from "../components/mainBox";
import useAsync, { getFeedPage, getTest } from "../customhook/useAsync";

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
function FeedPage() {
  // const [state, fetchData] = useAsync(() => getFeedPage(), true);
  const [state, fetchData] = useAsync(() => getTest("codyfeed"), true);
  const { loading, data, error } = state;

  const renderFeedList = () => {
    if (loading) return <div>loading...</div>;
    if (error) return <div>error message {error.message}</div>;
    if (!data) return null;
    // if (data) console.log(data);
    console.log(data);

    return (
      <>
        {data.map((data: any) => (
          <CodyContainer key={data.userid}>
            <img className="img" src={data.imgsrc}></img>
            <div>
              <span>{data.userid}</span>
              <span>
                <FontAwesomeIcon icon={faHeart} style={{ color: "#ff0000" }} />{" "}
                {data.wcount}
              </span>
            </div>
          </CodyContainer>
        ))}
      </>
    );
  };

  return (
    <MainContainer>
      {/* {renderFeedList()} */}
      {renderFeedList()}
    </MainContainer>
  );
}

export default FeedPage;
