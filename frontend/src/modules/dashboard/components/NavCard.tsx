import { NavLink } from "react-router-dom";

type Props = {
    label: string,
    value: number | string,
    link: string,
    color: string,
}

function NavCard({label, value, link, color}:Props){
    return(
    <NavLink
            key={label}
            to={link}
            className={`rounded-xl shadow p-5 ${color} hover:shadow-md transition`}>
            <p className="text-sm font-medium opacity-70">{label}</p>
            <p className="text-2xl font-bold mt-1">{value}</p>
    </NavLink>
    );
}

export default NavCard;