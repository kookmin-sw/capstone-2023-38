import React, { useCallback, useRef, useState } from "react";
import { useNavigate } from "react-router";
import styled from "styled-components";
import Button from "../components/button";
import MainBox from "../components/mainBox";
import GoogleLoginButton from "../google";

const Loginspan = styled.span`
  color: #fff;
  font-size: 2.5rem;
  margin-bottom: 2rem;
`;

const Notuser = styled.span`
  margin-top: 1rem;
  margin-left: auto;
  margin-right: auto;
  color: #fff;
`;

const Googlebtn = styled.div`
  margin-top: 1rem;
  margin-left: auto;
  margin-right: auto;
`;

const Input = styled.input`
  padding: 12px;
  border-radius: 1%;
  border: 1px solid #dee2e6;
  width: 100%;
  outline: none;
  font-size: 16px;
  box-sizing: border-box;
  margin-top: 1rem;
`;

function Login() {
  const [userInputs, setUserLogin] = useState({
    email: "",
    password: "",
  });
  const { email, password } = userInputs;
  const userInput = useRef<HTMLInputElement>(null);

  //아이디&비밀번호 입력값 받아오기
  const onChange = useCallback(
    (e: any) => {
      const { value, name } = e.target; // 우선 e.target 에서 name 과 value 를 추출
      setUserLogin({
        ...userInputs, // 기존의 input 객체를 복사한 뒤
        [name]: value, // name 키를 가진 값을 value 로 설정
      });
    },
    [userInputs]
  );

  //로그인 실패 시, 입력칸 초기화 및 포커스
  const onRset = useCallback(() => {
    setUserLogin({
      email: "",
      password: "",
    });
    if (userInput.current) userInput.current.focus();
  }, []);

  //mock-up 데이터
  const [users, setUsers] = useState([
    {
      id: "1",
      email: "test@test.com",
      password: "test1234",
    },
    {
      id: "2",
      email: "test2@test.com",
      password: "test1234",
    },
  ]);

  //아이디&비밀번호 확인
  const onCheck = useCallback(() => {
    const user = users.find(
      (user) => user.email === email && user.password === password
    );
    if (user) {
      console.log("로그인 성공");
      moveMyClosetPage();
    } else {
      alert("이메일 또는 비밀번호가 잘못되었습니다.");
      onRset();
    }
  }, [email, onRset, password, users]);

  //회원가입 페이지로 이동
  const navigte = useNavigate();
  const moveSignUpPage = () => {
    navigte("/signUp");
  };
  const moveMyClosetPage = () => {
    navigte("/myCloset");
  };

  return (
    <MainBox>
      <Loginspan>로그인</Loginspan>
      <Input
        name="email"
        placeholder="이메일 주소"
        onChange={onChange}
        value={email}
        ref={userInput}
      ></Input>
      <Input
        type="password"
        name="password"
        placeholder="비밀번호"
        onChange={onChange}
        value={password}
      ></Input>
      <Button onClick={onCheck}>로그인</Button>

      <Notuser onClick={moveSignUpPage}>회원이 아니신가요?</Notuser>
      <Googlebtn>
        <GoogleLoginButton></GoogleLoginButton>
      </Googlebtn>
    </MainBox>
  );
}

export default Login;
