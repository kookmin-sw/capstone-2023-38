import axios from "axios";
import React, { useState, useRef, useCallback, useEffect } from "react";
import styled from "styled-components";
import Button from "../components/button";
import MainBox from "../components/mainBox";
import useAsync, { createMyCloth } from "../customhook/useAsync";

const ipconfig = "192.168.35.177:8080";
interface ImageSrc {
  image: string;
  name: string;
  file: any;
}

interface Props {
  category: string;
}
const ButtonWrapper = styled.div`
  display: ${(props: { visible: boolean }) =>
    props.visible ? "contents" : "none"};
`;
const ImgBox = styled.div`
  display: flex;
  flex-direction: column;
  text-align: center;

  .img-div {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .img {
    max-width: 200px;
    max-height: 200px;
    margin-top: 2rem;
  }
`;

//input file upload 커스터마이징을 위해 display에서 숨기기
const FileUpload = styled.input`
  display: none;
`;

function ImgUpload({ category }: Props) {
  //이미지 소스 상태관리. 초기 상태를 빈 배열로 저장
  const [imageSrc, setImageSrc]: any = useState<ImageSrc[]>([]);

  const fileupload = useRef<HTMLInputElement>(null);

  //커스텀 버튼 클릭시, input 테그의 파일 업로드가 클릭되도록 focus를 맞춤
  const onClick = useCallback((e: any) => {
    if (!fileupload.current) {
      return;
    }
    fileupload.current.click();
  }, []);

  //이미지 선택하기
  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      if (!e.target.files) {
        return;
      }
      //올릴 수 있는 이미지 최대 5장
      const fileList = e.target.files;
      if (fileList.length > 5 || imageSrc.length > 5) {
        alert("5장 이상 업로드는 불가능 합니다!");
        return;
      }

      //객체의 불변성을 위해 배열 복사
      let imgUrlList: ImageSrc[] = [...imageSrc];

      for (let i = 0; i < fileList.length; i++) {
        //중복 이미지 체크
        const currentImageName = fileList[i].name;
        if (imgUrlList.find((img) => img.name === currentImageName)) continue;

        const currentImage = URL.createObjectURL(fileList[i]);
        // console.log(currentImage);
        // console.log(fileList[i]);

        imgUrlList.push({
          image: currentImage,
          name: currentImageName,
          file: fileList[i],
        });
      }
      setImageSrc(imgUrlList);
      // console.log(imageSrc);
    },
    [imageSrc]
  );

  console.log(imageSrc);
  //todo: 코드 최적화
  // const [state, fetchData] = useAsync(
  //   () => createMyCloth(imageSrc, category, "kookmin123"),
  //   false
  // );

  // const { loading, error } = state;
  // const imageUpload = useCallback(() => {
  //   if (loading) return <div>loading...</div>;
  //   if (error) return <div>error message {error.message}</div>;

  //   fetchData();
  // }, []);

  const handleUpload = async () => {
    const formData = new FormData();

    for (let i = 0; i < imageSrc.length; i++) {
      formData.append("files", imageSrc[i].file);
    }
    // formData.append("userid", "kookmin123");

    try {
      const response = await axios.post(
        `http://${ipconfig}/api/uploadTempCloset_TOP?userid=kookmin123`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      if (response.data === "top") alert("이미지 업로드가 완료되었습니다.");
      else
        alert(
          `해당 사진은 ${response.data} 카테고리입니다. 그곳으로 업로드 해주세요.`
        );
    } catch (error) {
      console.error(error);
      alert("이미지 업로드에 실패했습니다.");
    }
  };

  const jujack = () => {
    alert("이미지 업로드가 완료되었습니다");
  };

  const handleDeleteImage = useCallback(
    (name: any) => {
      setImageSrc(imageSrc.filter((img: any) => img.name !== name));
      console.log(imageSrc);
    },
    [imageSrc]
  );

  return (
    <div>
      {" "}
      <FileUpload
        type="file"
        accept="image/*"
        multiple={true}
        id="fileupload"
        ref={fileupload}
        onChange={handleChange}
      />
      <ButtonWrapper visible={imageSrc.length > 0}>
        <Button onClick={jujack}>사진 업로드</Button>
      </ButtonWrapper>
      <Button onClick={onClick}>이미지 선택하기</Button>
      {/* 저장해둔 이미지들을 순회하면서 화면에 이미지 출력 */}
      <ImgBox>
        {imageSrc.map(({ image, name }: any, index: number) => (
          <div className="img-div" key={index}>
            <img className="img" src={image} alt={`${image}-${name}`} />
            <Button
              fontcolor="palevioletred"
              onClick={() => handleDeleteImage(name)}
            >
              X
            </Button>
          </div>
        ))}
      </ImgBox>
    </div>
  );
}

export default ImgUpload;
