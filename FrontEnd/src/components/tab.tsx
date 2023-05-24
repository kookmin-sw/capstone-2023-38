import React, { useEffect, useState } from "react";
import styled from "styled-components";

interface Props {
  tablist: string[];
  colorlist?: string[];
  currentTab: string;
  currentIdx: number;
}
const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;

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
function Tabs({ tablist, colorlist, currentTab, currentIdx }: Props) {
  return (
    <Container>
      <ul className="tabs">
        {tablist.map((element, index) => {
          return (
            <li
              key={index}
              style={
                currentIdx === index && colorlist
                  ? {
                      fontWeight: "bold",
                      fontSize: "1.2rem",
                      backgroundColor: `#${colorlist[currentIdx]}`,
                    }
                  : { fontWeight: "normal" }
              }
            >
              {element}
            </li>
          );
        })}
      </ul>
    </Container>
  );
}

export default Tabs;
