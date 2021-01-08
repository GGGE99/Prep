import facade from "../facades/LoginFacade";
import React, { useState } from "react";
import { Jumbotron, Row, Col, Form } from "react-bootstrap";
import { useHistory } from "react-router-dom";
import { getUserByJwt, setToken } from "../utils/token";

function Signup({ setUser, setError, error }) {
  const init = { username: "", password1: "", password2: "" };
  const [loginCredentials, setLoginCredentials] = useState(init);

  let history = useHistory();

  const performLogin = (evt) => {
    evt.preventDefault();
    if (
      loginCredentials.password1.length > 0 &&
      loginCredentials.password2.length > 0 &&
      loginCredentials.username.length > 0
    ) {
      if (loginCredentials.password1 === loginCredentials.password2) {
        facade.signup(
          loginCredentials.username,
          loginCredentials.password1,
          (data) => {
            setToken(data.token);
            setUser({ ...getUserByJwt() });
            setError("");
            history.push("/");
          },
          setError
        );
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
            <Form.Control
              id="password1"
              type="Password"
              placeholder="Enter Password"
            />
            <Form.Label>Repeat Password</Form.Label>
            <Form.Control
              id="password2"
              type="Password"
              placeholder="Enter Password"
            />
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
