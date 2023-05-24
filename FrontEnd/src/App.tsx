import React from "react";
import "./App.css";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import Login from "./login/loginPage";
import SignUp from "./signup/signup";
import ImgUpload from "./imgUpload/imgUpload";
import MyCloset from "./myCloset/myCloset";
import MyWishList from "./myPage/myWishList";
import Header from "./components/header";
import BaseHeader from "./components/baseHeader";
import MainPage from "./mainPage";
import Recommend from "./recommend/recommendPage";
import FeedPage from "./feedPage/feedPage";

function App() {
  const location = useLocation();
  const baseHeader =
    location.pathname === "/login" || location.pathname === "/main";

  // 경로가 / 일때 /main으로 리다이렉트
  if (location.pathname === "/") {
    return <Navigate to="/main" replace />;
  }

  return (
    <>
      {baseHeader ? <BaseHeader /> : <Header />}
      <Routes>
        <Route path="/main" Component={MainPage}></Route>
        <Route path="/login" Component={Login} />
        <Route path="/signUp" Component={SignUp} />
        <Route path="/imgUpload" Component={Recommend} />
        <Route path="/myCloset" Component={MyCloset} />
        <Route path="/myWishList" Component={MyWishList} />
        <Route path="/FeedList" Component={FeedPage} />
      </Routes>
    </>
  );
}

export default App;
