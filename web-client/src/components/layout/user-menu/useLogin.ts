import { useTranslation } from 'react-i18next';
import { useAuth } from '../../AuthContext';
import { useLayout } from '../LayoutContext';
import { useEffect } from 'react';
import { Form, FormInstance, message } from 'antd';
const { useForm } = Form;

export function useLogin(): LoginHook {
  const [loginForm] = useForm<LoginForm>();
  const { t } = useTranslation(['user']);
  const { login } = useAuth();
  const { showUserMenu } = useLayout();

  useEffect(() => {
    return () => {
      loginForm.resetFields(['username', 'password']);
    };
  }, [showUserMenu]);

  async function onSubmit(data: LoginForm): Promise<void> {
    await login(data.username, data.password).catch(err => {
      console.error('Login failed', err);
      message.error(t('user:errors.login'));
    });
  }

  return {
    loginForm,
    onSubmit,
  };
}

interface LoginHook {
  loginForm: FormInstance<LoginForm>;
  onSubmit: (data: LoginForm) => Promise<void>;
}

interface LoginForm {
  username: string;
  password: string;
}
