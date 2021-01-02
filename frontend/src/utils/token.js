export function parseJwt(token) {
  var base64Url = token.split(".")[1];
  var base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
  var jsonPayload = decodeURIComponent(
    atob(base64)
      .split("")
      .map(function (c) {
        return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
      })
      .join("")
  );

  return JSON.parse(jsonPayload);
}

export const getToken = () => {
  return localStorage.getItem("jwtToken");
};

export const setToken = (token) => {
  localStorage.setItem("jwtToken", token);
};

export function getUserByJwt() {
  if (getToken()) {
  console.log(getToken())
    const tokenUser = parseJwt(getToken());
    const tempUser = {
      username: tokenUser.username,
      roles: [...tokenUser.roles.split(",")],
    };

    return tempUser
  }
}