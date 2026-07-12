import { Outlet } from "react-router-dom";

import Navbar from "../components/layout/Navbar";
import Sidebar from "../components/layout/Sidebar";
import Footer from "../components/layout/Footer";

import "./MainLayout.css";

function MainLayout() {
  return (
    <div className="layout">
      <Navbar />

      <div className="layout__body">
        <Sidebar />

        <main className="layout__content">
          <Outlet />
        </main>
      </div>

      <Footer />
    </div>
  );
}

export default MainLayout;
