import {
  Outlet,
  RouterProvider,
  createBrowserRouter
} from "react-router-dom";

import { ToastContainer } from "react-toastify";
import Admin from "./Components/Admin/Admin";
import Edit from "./Components/Edit/Edit";
import OldAdmin from "./Components/Admin/OldAdmin";

const Layout = () => {
  return (
      <div className="app">
          <Outlet/>
          <ToastContainer />
      </div>
  )
}

const router = createBrowserRouter([
  {
      path:"/",
      element: <Layout/>,
      children: [
          {
              path: "/admin",
              element: <Admin/>
          },
          {
            path: "/old-admin",
            element: <OldAdmin/>
        },
          {
              path: "/edit/:id",
              element: <Edit/>
          },
      ]
  },
])

export default function App() {
  return (
      <div>
          <RouterProvider router={router}/>
      </div>
  )
}