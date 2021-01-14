import facade from "../facades/fetchFacade";
import React, { useState, useEffect } from "react";

export default function Jokes({setError}) {
  const [dataFromServer, setDataFromServer] = useState({ isEmpty: true });
  useEffect(() => {
    facade.fetchData((data) => setDataFromServer(data), setError);
  }, []);

  return (
    <>
    <div className="text-center w-100">
      <h1 className="p-3" style={{borderBottom: 2+"px solid black"}}>Jokes</h1>
      {dataFromServer.isEmpty ? (
        <p>Loading..</p>
      ) : (
        <>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer[1]}</h3>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer[2]}</h3>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer[3]}</h3>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer[4]}</h3>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer[5]}</h3>
        </>
      )}
    </div>
    </>
  );
}
