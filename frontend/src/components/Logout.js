import { Button } from "bootstrap";
import React, { useState } from "react";
import { Jumbotron, Row, Col, Form } from "react-bootstrap";
import { Link } from "react-router-dom";

function LoginDisplay({ logout }) {
  return (
    <Row>
      <Col></Col>
      <Col>
        <Jumbotron>
          <h3>
            Du har været loget ind for længe og skal derfor loge ind igen
          </h3>
          <button
            className="btn btn-block btn-primary ml-2"
            onClick={() => {
              logout();
              window.location.href = "/signin";
            }}
          >
            Login again
          </button>
        </Jumbotron>
      </Col>
      <Col></Col>
    </Row>
  );
}

export default LoginDisplay;
