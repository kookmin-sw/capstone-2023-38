import React, { useCallback, useEffect, useState } from "react";
import useAsync, { getTest, getUsers } from "../customhook/useAsync";
import styled from "styled-components";
import ImageSlider from "../components/imgSlider";
import ClothData from "./clothData";
import { RingLoader } from "react-spinners";

interface Color {
  color?: string;
}
type TabProps = {
  active: boolean;
};

const Container = styled.div<TabProps>`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;

  .tabs {
    margin: 1rem;
    padding: 0px;
    list-style: none;
    color: #ffffff;
    background-color: black;
  }
  .tabs > li {
    margin: 1rem;
    display: inline-block;
    padding: 10px 15px;
    cursor: pointer;

    transition: font-size 0.3s ease-in-out;

    &:hover {
      font-size: 1.2rem;
    }
  }
`;

const SubContainer = styled.div`
  display: flex;
  flex-direction: column;
`;

const Category = styled.div<Color>`
  margin: 0 auto;
  margin-top: 2rem;
  padding: 10px;

  width: 100px;
  height: 100px;

  color: white;
  background-color: black;
  cursor: pointer;

  border-radius: 50%;

  text-align: center;
  align-item: center;

  transition: transform 0.3s ease-in-out;
  &:hover {
    transform: scale(1.1);
  }

  @media (max-width: 768px) {
    width: 50px;
    height: 50px;
  }
`;

function MyCloset() {
  const categories = ["TOP", "BOTTOM", "OUTER", "SHOES", "ACCESSORY"];

  const [category, setCategory] = useState(categories[0]);
  const [currentTab, setCurrentTab] = useState(0);

  const [state, fetchData] = useAsync(() => getTest("mycloset"), true);
  const [clothdata, setClothdata] = useState();
  const { data } = state;

  useEffect(() => {
    getMyCloset();
  }, [data]);

  const getMyCloset = () => {
    if (!data) console.log("loading..");
    if (data) {
      const getdata = () => {
        try {
          return data.images;
        } catch (e) {
          console.log(e);
        }
      };
      const closet = getdata();
      setTimeout(() => {
        setClothdata(closet);
      }, 5000);
    }
  };

  const TagClick = (idx: number) => {
    setCategory(categories[idx]);
    setCurrentTab(idx);
  };

  return (
    <>
      <Container active>
        <ul className="tabs">
          {categories.map((element, index) => {
            return (
              <li
                key={index}
                style={
                  currentTab === index
                    ? { fontWeight: "bold" }
                    : { fontWeight: "normal" }
                }
                onClick={() => TagClick(index)}
              >
                {element}
              </li>
            );
          })}
        </ul>
        {clothdata ? (
          <ClothData category={category} clothData={clothdata} />
        ) : (
          <div className="loading-container">
            <RingLoader color="#123abc" size={200} />
          </div>
        )}
      </Container>
    </>
  );
}

export default MyCloset;
