import "./Navbar.css";

function Navbar() {
  return (
    <header className="navbar">
      <div className="navbar__left">
        <h2 className="navbar__logo">KatibaAI</h2>
      </div>

      <div className="navbar__right">
        <button className="navbar__theme-button">🌙</button>

        <div className="navbar__avatar">N</div>
      </div>
    </header>
  );
}

export default Navbar;
