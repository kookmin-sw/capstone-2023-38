import React from "react";
import styled from "styled-components";

interface Props {
  children: React.ReactNode;
  fontcolor?: string;
  backcolor?: string;
  onClick?: any;
}

const StyledButton = styled.button<Props>`
  font-size: 1em;
  margin: 1em;
  padding: 0.25em 1em;
  border-radius: 3px;
  cursor: pointer;

  /* Color the border and text with theme.main */
  color: ${(props) => props.fontcolor || "black"};
  border: 2px solid ${(props) => props.fontcolor || "black"};
  background: #ffffff;

  &:hover {
    background: ${(props) => props.fontcolor || "black"};
    color: #ffffff;
  }
  &:active {
    color: ${(props) => props.fontcolor || "black"};
    background: #ffffff;
  }

  //etc
  & + & {
    margin-left: 1rem;
  }
  margin-top: 1rem;
`;

function Button({ onClick, children, ...rest }: Props) {
  return (
    <StyledButton {...rest} onClick={onClick}>
      {children}
    </StyledButton>
  );
}

export default Button;
