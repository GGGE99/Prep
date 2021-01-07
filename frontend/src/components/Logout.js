import { Button } from "bootstrap";
import React, { useState } from "react";
import { Jumbotron, Row, Col, Form } from "react-bootstrap";
import { Link } from "react-router-dom";

function LoginDisplay({}) {
  //   const init = { username: "", password: "" };
  //   const [loginCredentials, setLoginCredentials] = useState(init);

  //   const performLogin = (evt) => {
  //     evt.preventDefault();
  //     login(loginCredentials.username, loginCredentials.password);
  //   };
  //   const onChange = (evt) => {
  //     setLoginCredentials({
  //       ...loginCredentials,
  //       [evt.target.id]: evt.target.value,
  //     });
  //   };
  return (
    <Row>
        <h1>Du har været loget ind for længe og skal derfor loge ind igen :D</h1>
      <button>Logout!!!</button>
    </Row>
  );
}

export default LoginDisplay;
