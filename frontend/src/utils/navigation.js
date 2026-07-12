import { MessageCircle, History, User, Settings } from "lucide-react";

const navigation = [
  {
    id: "chat",
    label: "Chat",
    path: "/chat",
    icon: MessageCircle,
  },
  {
    id: "history",
    label: "History",
    path: "/history",
    icon: History,
  },
  {
    id: "profile",
    label: "Profile",
    path: "/profile",
    icon: User,
  },
  {
    id: "settings",
    label: "Settings",
    path: "/settings",
    icon: Settings,
  },
];

export default navigation;
