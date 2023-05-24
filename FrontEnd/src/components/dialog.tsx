import React, { useState } from "react";
import styled from "styled-components";
import Button from "./button";

const Overlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
`;

const DialogBox = styled.div`
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.5);

  width: 60%;
  height: 50%
  max-width: 80%;
  max-height: 80%;
  overflow: auto;
`;

const Title = styled.h2`
  margin-top: 0;
`;

function Dialog({ children, text, title, ...rest }: any) {
  const [isOpen, setIsOpen] = useState(false);
  return (
    <>
      <Button onClick={() => setIsOpen(true)}>{text}</Button>
      {isOpen && (
        <>
          <Overlay onClick={() => setIsOpen(false)} />
          <DialogBox>
            <Title>{title}</Title>
            {children}
            {/* <ImgUpload></ImgUpload> */}
            <Button fontcolor="palevioletred" onClick={() => setIsOpen(false)}>
              닫기
            </Button>
          </DialogBox>
        </>
      )}
    </>
  );
}

export default Dialog;
