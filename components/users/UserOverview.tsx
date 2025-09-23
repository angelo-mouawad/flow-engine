import { User } from '@types'

type Props = {
  users: User[]
}

const UserOverview: React.FC<Props> = ({ users }: Props) => {

  return (
    <>
      <section className="mt-5">
        <table>
          <thead>
            <tr>
              <th scope="col">Name</th>
              <th scope="col">Age</th>
            </tr>
          </thead>
          <tbody>
            {users &&
              users.length > 0 &&
              users.map((user, index) => (
                <tr key={index}>
                  <td>
                    {user.name}
                  </td>
                  <td>
                    {user.age}
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </section>
    </>
  )
}

export default UserOverview
