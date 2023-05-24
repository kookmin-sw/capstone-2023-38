import React from "react";
import styled from "styled-components";

interface Props {
  Uidata: any;
}

const WishContainer = styled.div`
  text-align: center;

  margin: 2rem;
  padding: 2rem;

  .img {
    max-width: 200px;
    max-height: 200px;
    border-radius: 0.2rem;
    // box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.5);
  }

  div > span {
    margin: 1rem;
  }
`;
function ImgView({ Uidata }: Props) {
  return;
}

export default ImgView;
