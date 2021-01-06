import React, { useState, useEffect } from "react";
import { Row, Col, Table } from "react-bootstrap";
import userFacade from "../facades/UserFacade";

export default function Users({ setError }) {
  const [users, setUsers] = useState([]);
  const [usersTable, setUsersTable] = useState([]);

  useEffect(() => {
    userFacade
      .fetchUsers()
      .then((data) => {
        setUsers([...data]);
        setUsersTable([...data])
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

  const editRole = (role, user) => {
    userFacade
      .editRole(role, user)
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

  function TD({ color, role, user }) {
    return (
      <td
        style={{ background: color }}
        onClick={(evt) => {
          evt.nativeEvent.originalTarget.style.background = "blue";
          editRole(role, user);
        }}
      ></td>
    );
  }

  const search = (evt) => {
    const val = evt.target.value
    let tempArr = []
    users.map((user) => {
      if(user.username.includes(val)){
        tempArr = [...tempArr, user]
      }
    })
    setUsersTable([...tempArr])
    console.log(evt.target.value)
  }

  const deleteUser = (username) => {
    console.log(username)
  }

  return (
    <Row>
      <Col></Col>
      <Col>
        <input
          type="text"
          class="form-control"
          id="searchInput"
          placeholder="Username"
          onChange={search}
        />
        <Table striped bordered hover variant="dark">
          <thead>
            <tr>
              <th>Username</th>
              <th>User</th>
              <th>Admin</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {usersTable.map((user) => {
              return (
                <tr>
                  <td>{user.username}</td>
                  {user.roles.includes("user") ? (
                    <TD color="green" role="user" user={user.username} />
                  ) : (
                    <TD color="red" role="user" user={user.username} />
                  )}
                  {user.roles.includes("admin") ? (
                    <TD color="green" role="admin" user={user.username} />
                  ) : (
                    <TD color="red" role="admin" user={user.username} />
                  )}
                  <td onClick={() => deleteUser(user.username)}>Delete</td>
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
