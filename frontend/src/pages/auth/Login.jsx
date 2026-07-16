import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import Card from "../../components/ui/Card";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";

import authService from "../../services/auth/authService";
import { useAuth } from "../../contexts/auth";

import styles from "./Auth.module.css";


function Login() {

  const navigate = useNavigate();

  const { login } = useAuth();


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


      const response = await authService.login(formData);


      if (!response.success) {

        setErrors({
          general: response.message,
        });

        return;

      }


      login(response);


      navigate("/chat", {
        replace: true,
      });


    } catch(error) {


      setErrors({
        general: "Unable to login. Please try again.",
      });


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


            {errors.general && (

                <p className={styles.error}>

                  {errors.general}

                </p>

            )}



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