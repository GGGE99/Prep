import { getToken, getCount, setToken } from "./token";
import { baseURL } from "./settings";
import { useHistory } from "react-router-dom";

export function handleHttpErrors(res) {
  if (!res.ok) {
    return Promise.reject({ status: res.status, fullError: res.json() });
  }
  return res.json();
}

export const makeOptions = (method, addToken, body) => {
  var opts = {
    method: method,
    headers: {
      "Content-type": "application/json",
      Accept: "application/json",
    },
  };
  if (addToken && getToken()) {
    console.log(typeof addToken);
    if (typeof addToken === "boolean") {
      opts.headers["x-access-token"] = getToken();
    } else {
      opts.headers["x-access-token"] = addToken;
    }
  }
  if (body) {
    opts.body = JSON.stringify(body);
  }
  return opts;
};

export const fetcher = (URL, options, action, setError, actionIfError) => {
  return fetch(URL, options)
    .then(handleHttpErrors)
    .then(action)
    .catch((err) => {
      if (err.status === 403) {
        refreshJWT(getCount())
          .then((data) => {
            console.log(options.body)
            setToken(data.token);
            options = makeOptions(options.method, data.token, options.body)
            console.log(options.body)
            fetch(URL, options)
              .then(handleHttpErrors)
              .then(action)
              .catch((err) => {
                if (actionIfError) actionIfError();
                else catcher(err, setError);
              });
          })
          .catch((err) => {
            if (err.status === 403) {
              //promt user to logout
              window.location.href = "/logout"
            }
            catcher(err, setError);
          });
      }
      if (actionIfError) actionIfError();
      else catcher(err, setError);
    });
};

const refreshJWT = () => {
  const options = makeOptions("POST", true, { count: getCount() });
  console.log(options);
  return fetch(baseURL + "api/user/test", options).then(handleHttpErrors);
};

const catcher = (err, setError) => {
  if (err.status) {
    err.fullError.then((e) => {
      setError(e.message);
    });
  } else {
    setError("Network error");
  }
};
