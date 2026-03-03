type Props = {
    title: string,
    value: number
}

function TileCard({title,value}:Props) {
  return (
    <div className="bg-white p-6 rounded-xl shadow-lg flex flex-col items-center justify-center">
            <div className="text-xl font-medium text-green-600">{title}</div>
            <span className="text-3xl font-bold text-gray-800">{value}</span>
    </div>
  )
}

export default TileCard