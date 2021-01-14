import React, { useState, useEffect } from "react";
import { Row, Col, Table } from "react-bootstrap";
import userFacade from "../facades/UserFacade";

export default function Users({ setError }) {
  const [users, setUsers] = useState([]);
  const [usersTable, setUsersTable] = useState([]);

  useEffect(() => {
    userFacade.fetchUsers(({ usersDTO }) => {
      setUsers([...usersDTO]);
      setUsersTable([...usersDTO]);
    }, setError);
  }, []);

  const editRole = (role, user) => {
    userFacade.editRole(role, user, (data) => console.log(data), setError);
  };

  const deleteUser = (username) => {
    userFacade.deleteUser(username, (data) => console.log(data), setError);
  };

  function TD({ color, role, user }) {
    return (
      <td
        style={{ background: color }}
        onClick={(evt) => {
          editRole(role, user);
          evt.nativeEvent.originalTarget.style.background = "blue";
        }}
      ></td>
    );
  }

  const search = (evt) => {
    const val = evt.target.value;
    let tempArr = [];
    users.map((user) => {
      if (user.username.includes(val)) {
        tempArr = [...tempArr, user];
      }
    });
    setUsersTable([...tempArr]);
    console.log(evt.target.value);
  };

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
                  {user.roles && user.roles.includes("user") ? (
                    <TD color="green" role="user" user={user.username} />
                  ) : (
                    <TD color="red" role="user" user={user.username} />
                  )}
                  {user.roles && user.roles.includes("admin") ? (
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
