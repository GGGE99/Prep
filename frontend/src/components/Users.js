import React, { useState, useEffect } from "react";
import { Row, Col, Table } from "react-bootstrap";
import userFacade from "../facades/UserFacade";

export default function Users({ setError }) {
  const [users, setUsers] = useState([]);
  useEffect(() => {
    userFacade
      .fetchUsers()
      .then((data) => {
        setUsers([...data]);
        console.log(data);
      })
      .catch((err) => {
        if (err.status) {
          err.fullError.then((e) => {
            setError(e.message);
          });
        } else {
          setError("Network error");
        }
      });
  }, []);

  const editRole = (role) => {
    userFacade
      .editRole(role)
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
  };

  function TD({ color, role }) {
    return (
      <td
        style={{ background: color }}
        onClick={(evt) => {
          evt.nativeEvent.originalTarget.style.background = "blue";
          editRole(role);
        }}
      ></td>
    );
  }

  return (
    <Row>
      <Col></Col>
      <Col>
        <Table striped bordered hover variant="dark">
          <thead>
            <tr>
              <th>Username</th>
              <th>User</th>
              <th>Admin</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => {
              return (
                <tr>
                  <td>{user.username}</td>
                  {user.roles.includes("user") ? (
                    <TD color="green" role="user" />
                  ) : (
                    <TD color="red" role="user" />
                  )}
                  {user.roles.includes("admin") ? (
                    <TD color="green" role="admin" />
                  ) : (
                    <TD color="red" role="admin" />
                  )}
                </tr>
              );
            })}
          </tbody>
        </Table>
      </Col>
      <Col></Col>

      <div className="text-center w-100">
        <h1>Users</h1>
      </div>
    </Row>
  );
}
