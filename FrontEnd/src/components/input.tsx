import React, { InputHTMLAttributes } from "react";
import styled from "styled-components";

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
}

const InputWrapper = styled.div`
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 300px;
  margin-bottom: 20px;
`;

const InputField = styled.input`
  flex: 1;
  padding: 10px;
  font-size: 16px;
  border: 2px solid #ccc;
  border-radius: 4px;
  transition: border-color 0.3s;

  &:focus {
    border-color: #4c9aff;
    outline: none;
  }
`;

const Label = styled.label`
  margin-right: 10px;
  font-size: 16px;
`;

const Input: React.FC<InputProps> = ({ label, ...props }) => {
  return (
    <InputWrapper>
      <Label>{label}</Label>
      <InputField {...props} />
    </InputWrapper>
  );
};

export default Input;
