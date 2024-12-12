import React from "react";
import { useAuth } from './common/authContext';
import { Button } from "@nextui-org/react";
import { Spacer } from "@nextui-org/react";
import Logout from './logout';
import { Divider } from "@nextui-org/divider";
import { Link } from "react-router-dom";

const SideBarContent = () => {
  const { isAuthenticated } = useAuth();

  return (
    <>
      {isAuthenticated && (
        <div className="max-w-md">
          <div className="space-y-1">
            <Link to="/" color="foreground">
              <h4 className="text-medium font-medium">Animals</h4>
            </Link>
          </div>
          <Divider className="my-4" />
          <div className="space-y-1">
            <Link to="/allvaccinations" color="foreground">
              <h4 className="text-medium font-medium">Vaccinations</h4>
            </Link>
          </div>
          <Divider className="my-4" />
          <Logout />
        </div>
      )}
    </>
  );
};

export default SideBarContent;
