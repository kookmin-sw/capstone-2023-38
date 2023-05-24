import React, { useState } from "react";
import styled from "styled-components";
import Select from "./select";

const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;
  border: 1px solid black;
  border-radius: 1%;
  width: 80%;
  margin: 1rem auto;

  span {
    font-family: "NanumSquareRound";
    font-style: normal;
    margin-top: 2rem;
    margin-bottom: 2rem;
    font-size: 1.5rem;
  }

  .tabs {
    margin: 1rem 0 3rem 0;
    padding: 0px;
    list-style: none;
    color: #black;
    background-color: #ffffff;
    display: flex; /* flexbox 사용 */
    justify-content: space-between; /* 요소들 사이에 공간을 만듦 */
  }
  .tabs > li {
    margin: 1rem;
    display: inline-block;
    padding: 10px 10px;
    cursor: pointer;

    transition: font-size 0.3s ease-in-out;

    &:hover {
      font-size: 1.2rem;
    }
  }
`;

function Recommend() {
  //   const [isOpen, setIsOpen] = useState(false);
  const seasons = ["SPRING", "SUMMER", "AUTUMN", "WINTER"];
  const color = ["ffc5dc", "8FCEFF", "bc944c", "9bb6b9"];
  const categories = ["TOP", "BOTTOM", "OUTER", "SHOES", "ACCESSORY"];

  const [season, setSeason] = useState(seasons[0]);
  const [currentSeason, setCurrentSeason] = useState(0);

  const [category, setCategory] = useState(categories[0]);
  const [currentCategory, setCurrentCategory] = useState(0);

  const SeasonClick = (idx: number) => {
    setSeason(seasons[idx]);
    setCurrentSeason(idx);
  };

  const handleCategory = (idx: number) => {
    setCategory(categories[idx]);
    setCurrentCategory(idx);
  };

  return (
    <>
      <Container>
        <span>
          <b>"계절"을 선택해주세요</b>
        </span>
        <ul className="tabs">
          {seasons.map((element, index) => {
            return (
              <li
                key={index}
                style={
                  currentSeason === index
                    ? {
                        fontWeight: "bold",
                        fontSize: "1.2rem",
                        backgroundColor: `#${color[currentSeason]}`,
                      }
                    : { fontWeight: "normal" }
                }
                onClick={() => SeasonClick(index)}
              >
                {element}
              </li>
            );
          })}
        </ul>
        <span>
          <b>"함께" 추천받을 옷을 골라주세요</b>
        </span>

        <ul className="tabs">
          {categories.map((element, index) => {
            return (
              <li
                key={index}
                style={
                  currentCategory === index
                    ? {
                        fontWeight: "bold",
                        fontSize: "1.2rem",
                      }
                    : { fontWeight: "normal" }
                }
                onClick={() => handleCategory(index)}
              >
                {element}
              </li>
            );
          })}
        </ul>
        <Select category={category} season={season}></Select>
      </Container>
    </>
  );
}

export default Recommend;
