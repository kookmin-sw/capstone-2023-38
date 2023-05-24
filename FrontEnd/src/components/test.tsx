import React from "react";
import useAsync, { getTest } from "../customhook/useAsync";
import ImageSlider from "./imgSlider";

interface Props {
  category: string;
}
function RenderUserData({ category }: Props) {
  const [state, fetchData] = useAsync(() => getTest("mycloset"), true);
  const { loading, data, error } = state;

  if (loading) return <div>loading...</div>;
  if (error) return <div>error message {error.message}</div>;
  if (!data) return <div>저장된 옷이 없습니다</div>;
  // if (data) console.log(data.images);
  const check = () => {
    try {
      if (category === "TOP") return data.images.top;
      if (category === "BOTTOM") return data.images.pants;
      if (category === "OUTER") return data.images.outer;
      if (category === "SHOES") return data.images.shoes;
    } catch (e: any) {
      console.log(e);
    }
  };
  const uiData = check();
  // console.log(uiData);
  // const userIds = data.map((user: any) => user.userId);
  if (!uiData) return <div>{category} 카테고리에 등록된 옷이 없습니다.</div>;

  return <ImageSlider images={uiData}></ImageSlider>;
}

export default RenderUserData;
