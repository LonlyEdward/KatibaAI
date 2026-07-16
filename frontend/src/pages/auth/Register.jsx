import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import Card from "../../components/ui/Card";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";

import authService from "../../services/auth/authService";

import styles from "./Auth.module.css";


function Register() {

  const navigate = useNavigate();


  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
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


    if (!formData.username) {

      newErrors.username = "Username is required";

    }


    if (!formData.email) {

      newErrors.email = "Email is required";

    }


    if (!formData.password) {

      newErrors.password = "Password is required";

    }


    if (
        formData.password &&
        formData.password.length < 6
    ) {

      newErrors.password =
          "Password must be at least 6 characters";

    }



    if (
        formData.password !== formData.confirmPassword
    ) {

      newErrors.confirmPassword =
          "Passwords do not match";

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


      const response = await authService.register({

        username: formData.username,

        email: formData.email,

        password: formData.password,

      });



      if (!response.success) {

        setErrors({

          general: response.message,

        });

        return;

      }


      navigate("/login");


    } catch(error) {


      setErrors({

        general: "Registration failed. Try again.",

      });


    } finally {


      setLoading(false);


    }

  }



  return (

      <div className={styles.authPage}>


        <Card

            title="Create Account"

            subtitle="Join KatibaAI and explore the Constitution with AI"

        >


          <form

              className={styles.form}

              onSubmit={handleSubmit}

          >



            <Input

                label="Username"

                name="username"

                placeholder="Enter username"

                value={formData.username}

                onChange={handleChange}

                error={errors.username}

                required

            />



            <Input

                label="Email"

                name="email"

                type="email"

                placeholder="Enter email"

                value={formData.email}

                onChange={handleChange}

                error={errors.email}

                required

            />



            <Input

                label="Password"

                name="password"

                type="password"

                placeholder="Create password"

                value={formData.password}

                onChange={handleChange}

                error={errors.password}

                required

            />



            <Input

                label="Confirm Password"

                name="confirmPassword"

                type="password"

                placeholder="Repeat password"

                value={formData.confirmPassword}

                onChange={handleChange}

                error={errors.confirmPassword}

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

              Register

            </Button>




            <p className={styles.footerText}>


              Already have an account?


              {" "}


              <Link to="/login">

                Login

              </Link>


            </p>



          </form>


        </Card>


      </div>

  );

}


export default Register;