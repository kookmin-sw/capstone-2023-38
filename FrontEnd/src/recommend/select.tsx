import axios from "axios";
import React, { useCallback, useEffect, useState } from "react";
import styled, { css } from "styled-components";
import { RingLoader } from "react-spinners";
import Button from "../components/button";
import useAsync, {
  getData,
  getTest,
  getUsers,
  RecommendPost,
} from "../customhook/useAsync";
import ViewCloth from "./viewcloth";

interface imgList {
  id: number;
  imgsrc: string;
  path: string;
}

interface DATA {
  imageUrls: string[];
  paths: string[];
  userId: string;
  season: string;
}

interface Props {
  category: string;
  season: string;
}

const Container = styled.div`
  width: 45%;
  border-radius: 1%;
  align-items: center;
  text-align: center;
  box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.5);
  margin: 0.5rem;
`;

const SelectBox = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;

  margin: 3rem 1.2rem 1.2rem 1.2rem;
`;

const ImgContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-center: center;
  text-align: center;

  img {
    width: 200px;
    height: 200px;
    margin: 1rem auto;
  }
`;

const override = css`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
`;

function Select({ category, season }: Props) {
  const [state, fetchData] = useAsync(() => getTest("mycloset"), true);
  // const [state, fetchData] = useAsync(
  //   () => getData("getMycloset", "kookmin123"),
  //   true
  // );

  const { loading, error, data } = state;

  const [selectedList, setSelectedList] = useState<imgList[]>([]);
  const [postdata, setPostData] = useState<DATA>();

  const [getimgdata, setGetData] = useState([]);

  let imgsrc;

  useEffect(() => {
    console.log(postdata);
    // handleSelectListChange(selectedList);
  }, [selectedList, postdata]);

  //여기서 viewcloth가 넘겨주는거 받아오는거고
  const handleSelectListChange = (selectlist: imgList[]) => {
    setSelectedList(selectlist);
    // console.log(selectedList);

    const imgUrls = selectedList.map((img) => img.imgsrc);
    const paths = selectedList.map((img) => img.path);

    const temp: DATA = {
      imageUrls: imgUrls,
      paths: paths,
      userId: "kookmin123",
      season: season,
    };
    setPostData(temp);
    // console.log(postdata);

    // 선택된 리스트에 대한 추가 처리나 상태 업데이트를 수행할 수 있습니다.
    // 예: 다른 상태 값 변경, API 호출 등
  };

  // const [postState, PostFunct] = useAsync(() => RecommendPost(postdata), false);

  // const imageUpload = useCallback(() => {
  //   PostFunct();
  // }, []);
  const [recoimg, setrecoimg] = useState("");
  const recoResult = () => {
    // 로딩 상태 변경
    setrecoimg("loading");

    // 5초 후에 이미지를 보여줌
    setTimeout(() => {
      setrecoimg(
        "https://image.msscdn.net/images/codimap/detail/22974/detail_22974_1_500.jpg?202305231805"
      );
    }, 5000);
  };

  const sendDataToServer = async () => {
    imgsrc =
      "https://image.msscdn.net/images/codimap/detail/22974/detail_22974_1_500.jpg?202305231805";
    console.log(postdata);
    try {
      const response = await axios.post(
        "http://10.30.116.50:8080/api/uploadRecStart",
        postdata,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      console.log(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const renderUserData = () => {
    if (loading) return <div>loading...</div>;
    if (error) return <div>error message {error.message}</div>;
    if (!data) return <div>저장된 옷이 없습니다</div>;
    // if (data) console.log(data.images);
    const check = () => {
      try {
        return data.images[category.toLowerCase()];
      } catch (e: any) {
        console.log(e);
      }
    };
    const uiData = check();
    // const userIds = data.map((user: any) => user.userId);
    if (!uiData) return <div>{category} 카테고리에 등록된 옷이 없습니다.</div>;

    return (
      <ViewCloth
        data={uiData}
        state={selectedList}
        category={category}
        onSelectListChange={handleSelectListChange}
      ></ViewCloth>
    );
  };
  //   console.log(data);

  const handleClickImg = async (img: any) => {
    if (window.confirm("선택에서 제외하시겠습니까?")) {
      let templist = selectedList.filter((item) => item.imgsrc !== img.imgsrc);
      console.log([...templist]);
      await setSelectedList([...templist]);
      console.log(selectedList);
    }
  };

  const recoClick = () => {
    if (window.confirm("내 위시리스트에 추가하시겠습니까?")) {
      alert("저장되었습니다");
    }
  };

  return (
    <>
      <Container>
        <h2>{category}</h2>
        {renderUserData()}
      </Container>
      <div style={{ display: "flex", flexDirection: "row" }}>
        {selectedList.length > 0 && (
          <SelectBox>
            <span>현재 선택한 옷</span>
            <ImgContainer>
              {selectedList.map((item) => (
                <img
                  onClick={() => handleClickImg(item)}
                  className="image"
                  key={item.id}
                  src={item.imgsrc}
                ></img>
              ))}
            </ImgContainer>
          </SelectBox>
        )}
        <SelectBox>
          {recoimg !== "" && (
            // 이미지 표시
            <>
              <span>추천 이미지</span>
              <ImgContainer>
                {recoimg === "loading" ? (
                  <div className="loading-container">
                    <RingLoader color="#123abc" size={100} />
                  </div>
                ) : (
                  <img src={recoimg} onClick={recoClick}></img>
                )}
              </ImgContainer>
            </>
          )}
        </SelectBox>
      </div>
      <Button onClick={recoResult}>추천받기</Button>
    </>
  );
}
export default Select;
