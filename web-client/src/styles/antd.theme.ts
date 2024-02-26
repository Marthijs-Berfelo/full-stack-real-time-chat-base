import variables from './variables.module.scss';
import { ThemeConfig } from "antd";

const borderRadius = 2;
const buttonRadius = 4;

export const andTheme: ThemeConfig = {
    token: {
        colorText: variables.colorText,
        colorPrimary: variables.colorPrimary,
        colorPrimaryBorder: variables.colorPrimaryBorder,
        colorBorder: variables.colorText,
        colorTextBase: variables.colorText,
        colorBgLayout: variables.colorWhite,
        borderRadius: borderRadius,
    },
    components: {
        Select: {
            borderRadius: borderRadius,
            borderRadiusSM: borderRadius,
            borderRadiusLG: borderRadius,
        },
        Button: {
            primaryColor: variables.colorWhite,
            borderRadius: buttonRadius,
        },
        Input: {
            borderRadius: buttonRadius,
        },
        InputNumber: {
            borderRadius: buttonRadius,
        },
        Tooltip: {
            borderRadius: buttonRadius,
        },
        Menu: {
            borderRadius: borderRadius,
        },
        Alert: {
            colorInfo: variables.colorInfoText,
            colorInfoBg: variables.colorInfoBG,
            colorInfoBorder: variables.colorInfoText,

            colorSuccess: variables.colorSuccessText,
            colorSuccessBg: variables.colorSuccessBG,
            colorSuccessBorder: variables.colorSuccessText,

            colorWarning: variables.colorWarningText,
            colorWarningBg: variables.colorWarningBG,
            colorWarningBorder: variables.colorWarningText,

            colorError: variables.colorErrorText,
            colorErrorBg:  variables.colorErrorBG,
            colorErrorBorder: variables.colorErrorText,
        },
    },
};