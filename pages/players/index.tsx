import Header from '@components/header'
import UserOverview from '@components/users/UserOverview'
import UserService from '@services/UserService'
import Head from 'next/head'
import useSWR from 'swr'
import type { User } from '@types'
import type { InferGetServerSidePropsType, GetServerSideProps } from 'next'

export const getServerSideProps  = async () => {
  const userResponse = await UserService.getAll()
  const users = await userResponse.json()
  return { props: { users } }
}
const Users: React.FC = ({ users }: InferGetServerSidePropsType<typeof getServerSideProps>) => {
  return (
    <>
      <Head>
        <title>Players</title>
      </Head>
      <Header />
      <main className="p-6 min-h-screen flex flex-col items-center">
        <h1>Players</h1>

        <section className="mt-5">
          {users && <UserOverview users={users} />}
        </section>
      </main>
    </>
  )
}

export default Users
