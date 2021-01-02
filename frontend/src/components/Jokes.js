import facade from "../facades/fetchFacade";
import React, { useState, useEffect } from "react";

export default function Jokes() {
  const [dataFromServer, setDataFromServer] = useState({ isEmpty: true });
  useEffect(() => {
    facade.fetchData().then((data) => setDataFromServer(data));
  }, []);

  return (
    <div className="text-center w-100">
      <h1 className="p-3" style={{borderBottom: 2+"px solid black"}}>Jokes</h1>
      {dataFromServer.isEmpty ? (
        <p>Loading..</p>
      ) : (
        <>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer.joke1}</h3>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer.joke2}</h3>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer.joke3}</h3>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer.joke4}</h3>
          <h3 className="p-3" style={{borderBottom: 2+"px solid black"}}>{dataFromServer.joke5}</h3>
        </>
      )}
    </div>
  );
}
