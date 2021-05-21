<template>
  <nav class="navbar p-2" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
      <a class="navbar-item" href="/">
        <img
          src="@/assets/logo.png"
        />
      </a>

      <a
        role="button"
        class="navbar-burger"
        aria-label="menu"
        aria-expanded="false"
        data-target="navbarBasicExample"
      >
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
      </a>
    </div>

    <div id="navbarBasicExample" class="navbar-menu">
      <div class="navbar-start">
        <router-link to="/" class="navbar-item"> Repertoire </router-link>
        <router-link to="/mychoir" class="navbar-item"> My Choir </router-link>
        <div class="navbar-item has-dropdown is-hoverable">
          <a class="navbar-link"> Settings </a>
          <div class="navbar-dropdown">
            <router-link to="/categories" class="navbar-item"> Categories </router-link>
            <hr class="navbar-divider" />
            <router-link to="/about" class="navbar-item"> About </router-link>
            <router-link to="/report" class="navbar-item"> Report an issue </router-link>
          </div>
        </div>
      </div>

      <div class="navbar-end">
        <a class="navbar-item"> My account </a>
        <div class="navbar-item">
          <div class="buttons">
            <a class="button is-primary">
              <strong>Sign up</strong>
            </a>
            <a class="button is-light"> Log in </a>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>