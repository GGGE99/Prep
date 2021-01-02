import facade from "../facades/LoginFacade";
import React, { useState, useEffect } from "react";
import { Jumbotron, Row, Col, Form } from "react-bootstrap";

function Signup() {
  const init = { username: "", password1: "", password2: "" };
  const [loginCredentials, setLoginCredentials] = useState(init);
  const [error, setError] = useState("");

  const performLogin = (evt) => {
    evt.preventDefault();
    if (
      loginCredentials.password1.length > 0 &&
      loginCredentials.password2.length > 0 &&
      loginCredentials.username.length > 0
    ) {
      if (loginCredentials.password1 === loginCredentials.password2) {
        facade
          .signup(loginCredentials.username, loginCredentials.password1)
          .then((data) => console.log(data))
          .catch((err) => {
            if (err.status) {
              err.fullError.then((e) => {
                setError(e.message);
              });
            } else {
              setError("Network error");
            }
          });
      } else {
        setError("Password doesn't match");
      }
    } else {
      setError("You must write in all fields");
    }
  };
  const onChange = (evt) => {
    setLoginCredentials({
      ...loginCredentials,
      [evt.target.id]: evt.target.value,
    });
  };

  return (
    <Row>
      <Col></Col>
      <Col>
        <Jumbotron className="mt-2 text-center">
          <h2>Sign-up</h2>
          <Form.Group controlId="formBasicEmail" onChange={onChange}>
            <Form.Label>Name</Form.Label>
            <Form.Control id="username" type="name" placeholder="Enter name" />
            <Form.Label>Password</Form.Label>
            <Form.Control id="password1" type="Password" placeholder="Enter Password" />
            <Form.Label>Repeat Password</Form.Label>
            <Form.Control id="password2" type="Password" placeholder="Enter Password" />
            <button className="btn btn-primary m-2" onClick={performLogin}>
              Sign UP
            </button>
          </Form.Group>
          <p>{error}</p>
        </Jumbotron>
      </Col>
      <Col></Col>
    </Row>
  );
}

export default Signup;
