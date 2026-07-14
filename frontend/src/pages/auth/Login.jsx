import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import Card from "../../components/ui/Card";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";

import styles from "./Auth.module.css";


function Login() {

  const navigate = useNavigate();


  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });


  const [errors, setErrors] = useState({});


  const [loading, setLoading] = useState(false);



  function handleChange(e) {

    const { name, value } = e.target;

    setFormData({
      ...formData,
      [name]: value,
    });

  }



  function validate() {

    const newErrors = {};


    if (!formData.email) {

      newErrors.email = "Email is required";

    }


    if (!formData.password) {

      newErrors.password = "Password is required";

    }


    return newErrors;

  }



  async function handleSubmit(e) {

    e.preventDefault();


    const validationErrors = validate();


    if (Object.keys(validationErrors).length > 0) {

      setErrors(validationErrors);

      return;

    }


    setErrors({});


    try {

      setLoading(true);


      /*
          Later:

          await authService.login(formData)

          AuthContext update

          navigate("/chat")
      */


      console.log("Login data:", formData);


      // Temporary navigation test
      navigate("/chat");


    } catch (error) {

      console.error(error);

    } finally {

      setLoading(false);

    }

  }



  return (

      <div className={styles.authPage}>

        <Card
            title="Welcome Back"
            subtitle="Sign in to continue using KatibaAI"
        >


          <form
              className={styles.form}
              onSubmit={handleSubmit}
          >


            <Input

                label="Email"

                name="email"

                type="email"

                placeholder="Enter your email"

                value={formData.email}

                onChange={handleChange}

                error={errors.email}

                required

            />



            <Input

                label="Password"

                name="password"

                type="password"

                placeholder="Enter your password"

                value={formData.password}

                onChange={handleChange}

                error={errors.password}

                required

            />



            <Button

                type="submit"

                fullWidth

                loading={loading}

            >

              Login

            </Button>



            <p className={styles.footerText}>

              Don't have an account?

              {" "}

              <Link to="/register">

                Register

              </Link>


            </p>


          </form>


        </Card>


      </div>

  );

}


export default Login;