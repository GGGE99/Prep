import React, { useState } from "react";
import { Jumbotron, Row, Col, Form } from "react-bootstrap";
import { Link } from "react-router-dom";

function LoginDisplay({ login, user, logout }) {
  const init = { username: "", password: "" };
  const [loginCredentials, setLoginCredentials] = useState(init);

  const performLogin = (evt) => {
    evt.preventDefault();
    login(loginCredentials.username, loginCredentials.password);
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
          {!user.username ? (
            <>
              <Form.Group controlId="formBasicEmail" onChange={onChange}>
                <Form.Label>Name</Form.Label>
                <Form.Control
                  id="username"
                  type="name"
                  placeholder="Enter name"
                />
                <Form.Label>Password</Form.Label>
                <Form.Control
                  id="password"
                  type="Password"
                  placeholder="Enter Password"
                />

                <button className="btn btn-primary m-2" onClick={performLogin}>
                  login
                </button>
              </Form.Group>
              <Link to="/signup">Sign-up</Link>
            </>
          ) : (
            <div>
              <h1>username: {user.username}</h1>

              <button className="btn btn-danger" onClick={logout}>
                Logout
              </button>
            </div>
          )}
        </Jumbotron>
      </Col>
      <Col></Col>
    </Row>
  );
}

export default LoginDisplay;
