import React, { useCallback } from "react";
import { GoogleLogin } from "@react-oauth/google";
import { GoogleOAuthProvider } from "@react-oauth/google";
import { useNavigate } from "react-router-dom";

const GoogleLoginButton = () => {
  // const apiKey = process.env.REACT_APP_CLIENT_ID;
  // console.log(process.env.ClientId);

  const navigte = useNavigate();
  const moveImgUploadPage = useCallback(() => {
    navigte("/myCloset");
  }, [navigte]);

  return (
    <GoogleOAuthProvider clientId="117834358417-g87ukt17qpekli2bc9b4nbb8kohrfcaq.apps.googleusercontent.com">
      <div>
        <GoogleLogin
          onSuccess={(credentialResponse) => {
            console.log(credentialResponse);
            moveImgUploadPage();
          }}
          onError={() => {
            console.log("Login Failed");
          }}
        />
      </div>
    </GoogleOAuthProvider>
  );
};

export default GoogleLoginButton;
