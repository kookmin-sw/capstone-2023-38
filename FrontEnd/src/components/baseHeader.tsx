import React, { useCallback, useEffect, useRef } from "react";
import styled from "styled-components";
// import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// import { faCircleUser } from "@fortawesome/free-solid-svg-icons";
import logoImg from "../img/logo.png";
import { useNavigate } from "react-router-dom";
import GoogleLoginButton from "../google";

const Navigationbar = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 100%;
  margin-left: 100px;
  margin-right: 100px;

  .logo {
    max-width: 250px;
    max-hegth: 100px;
    transition: max-width 0.3s ease-in-out;

    &:hover {
      max-width: 270px;
    }
  }

  .menubox {
    float: right;
    align-itmes: center;
  }

  .menu {
    margin: 2rem;
    font-size: 1.3rem;
    font-weight: semi-bold;
    color: black;
    cursor: pointer;
    transition: font-size 0.3s ease-in-out;

    &:hover {
      font-size: 1.6rem;
      border-bottom: 1px solid white;
      padding-bottom: 2px;
    }
  }

  nav > ul {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: space-between;
    list-style: none;
  }

  nav > ul > li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 1.5rem;
    font-size: 15px;

    cursor: pointer;
    transition: font-size 0.3s ease-in-out;

    &:hover {
      font-size: 18px;
      border-bottom: 1px solid white;
      padding-bottom: 2px;
      font-weight: semi-bold;
    }
  }
`;

function BaseHeader() {
  const navigator = useNavigate();

  const moveMainPage = useCallback(() => {
    navigator("/main");
  }, [navigator]);

  return (
    <Navigationbar>
      <img src={logoImg} className="logo" onClick={moveMainPage} />
      <nav>
        <ul>
          <li>ABOUT</li>
          <li>STYLE</li>
        </ul>
      </nav>
    </Navigationbar>
  );
}

export default BaseHeader;
