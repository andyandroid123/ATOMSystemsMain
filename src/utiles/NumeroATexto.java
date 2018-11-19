package utiles;

public class NumeroATexto {

    private int flag;
    public long numero;
    public String importe_parcial;
    public String num;
    public String num_letra;
    public String num_letras;
    public String num_letram;
    public String num_letradm;
    public String num_letracm;
    public String num_letramm;
    public String num_letradmm;

    public NumeroATexto() {
        numero = 0;
        flag = 0;
    }

    public NumeroATexto(long n) {
        numero = n;
        flag = 0;
    }

    private String unidad(long numero) {
        if (numero == 9) {
            num = "nueve";
        } else if (numero == 8) {
            num = "ocho";
        } else if (numero == 7) {
            num = "siete";
        } else if (numero == 6) {
            num = "seis";
        } else if (numero == 5) {
            num = "cinco";
        } else if (numero == 4) {
            num = "cuatro";
        } else if (numero == 3) {
            num = "tres";
        } else if (numero == 2) {
            num = "dos";
        } else if (numero == 1) {
            if (flag == 0) {
                num = "uno";
            } else {
                num = "un";
            }
        } else if (numero == 0) {
            num = "";
        }
        return num;
    }

    private String decena(long numero) {

        if (numero >= 90 && numero <= 99) {
            num_letra = "noventa ";
            if (numero > 90) {
                num_letra = num_letra.concat("y ").concat(unidad(numero - 90));
            }
        } else if (numero >= 80 && numero <= 89) {
            num_letra = "ochenta ";
            if (numero > 80) {
                num_letra = num_letra.concat("y ").concat(unidad(numero - 80));
            }
        } else if (numero >= 70 && numero <= 79) {
            num_letra = "setenta ";
            if (numero > 70) {
                num_letra = num_letra.concat("y ").concat(unidad(numero - 70));
            }
        } else if (numero >= 60 && numero <= 69) {
            num_letra = "sesenta ";
            if (numero > 60) {
                num_letra = num_letra.concat("y ").concat(unidad(numero - 60));
            }
        } else if (numero >= 50 && numero <= 59) {
            num_letra = "cincuenta ";
            if (numero > 50) {
                num_letra = num_letra.concat("y ").concat(unidad(numero - 50));
            }
        } else if (numero >= 40 && numero <= 49) {
            num_letra = "cuarenta ";
            if (numero > 40) {
                num_letra = num_letra.concat("y ").concat(unidad(numero - 40));
            }
        } else if (numero >= 30 && numero <= 39) {
            num_letra = "treinta ";
            if (numero > 30) {
                num_letra = num_letra.concat("y ").concat(unidad(numero - 30));
            }
        } else if (numero >= 20 && numero <= 29) {
            if (numero == 20) {
                num_letra = "veinte ";
            } else {
                num_letra = "veinti".concat(unidad(numero - 20));
            }
        } else if (numero >= 10 && numero <= 19) {
            if (numero == 10) {
                num_letra = "diez ";
            } else if (numero == 11) {
                num_letra = "once ";
            } else if (numero == 12) {
                num_letra = "doce ";
            } else if (numero == 13) {
                num_letra = "trece ";
            } else if (numero == 14) {
                num_letra = "catorce ";
            } else if (numero == 15) {
                num_letra = "quince ";
            } else if (numero == 16) {
                num_letra = "dieciseis ";
            } else if (numero == 17) {
                num_letra = "diecisiete ";
            } else if (numero == 18) {
                num_letra = "dieciocho ";
            } else if (numero == 19) {
                num_letra = "diecinueve ";
            }
        } else {   
            num_letra = unidad(numero);
        }
        return num_letra;
    }

    private String centena(long numero) {
        if (numero >= 100) {
            if (numero >= 900 && numero <= 999) {
                num_letra = "novecientos ";
                if (numero > 900) {
                    num_letra = num_letra.concat(decena(numero - 900));
                }
            } else if (numero >= 800 && numero <= 899) {
                num_letra = "ochocientos ";
                if (numero > 800) {
                    num_letra = num_letra.concat(decena(numero - 800));
                }
            } else if (numero >= 700 && numero <= 799) {
                num_letra = "setecientos ";
                if (numero > 700) {
                    num_letra = num_letra.concat(decena(numero - 700));
                }
            } else if (numero >= 600 && numero <= 699) {
                num_letra = "seiscientos ";
                if (numero > 600) {
                    num_letra = num_letra.concat(decena(numero - 600));
                }
            } else if (numero >= 500 && numero <= 599) {
                num_letra = "quinientos ";
                if (numero > 500) {
                    num_letra = num_letra.concat(decena(numero - 500));
                }
            } else if (numero >= 400 && numero <= 499) {
                num_letra = "cuatrocientos ";
                if (numero > 400) {
                    num_letra = num_letra.concat(decena(numero - 400));
                }
            } else if (numero >= 300 && numero <= 399) {
                num_letra = "trescientos ";
                if (numero > 300) {
                    num_letra = num_letra.concat(decena(numero - 300));
                }
            } else if (numero >= 200 && numero <= 299) {
                num_letra = "doscientos ";
                if (numero > 200) {
                    num_letra = num_letra.concat(decena(numero - 200));
                }
            } else if (numero >= 100 && numero <= 199) {
                if (numero == 100) {
                    num_letra = "cien ";
                } else {
                    num_letra = "ciento ".concat(decena(numero - 100));
                }
            }
        } else {
            num_letra = decena(numero);
        }
        return num_letra;
    }

    private String miles(long numero) {
        if (numero >= 1000 && numero < 2000) {
            num_letram = ("mil ").concat(centena(numero % 1000));
        }
        if (numero >= 2000 && numero < 10000) {
            flag = 1;
            num_letram = unidad(numero / 1000).concat(" mil ").concat(centena(numero % 1000));
        }
        if (numero < 1000) {
            num_letram = centena(numero);
        }
        return num_letram;
    }

    private String decmiles(long numero) {
        if (numero == 10000) {
            num_letradm = "diez mil";
        }
        if (numero > 10000 && numero < 20000) {
            flag = 1;
            num_letradm = decena(numero / 1000).concat("mil ").concat(centena(numero % 1000));
        }
        if (numero >= 20000 && numero < 100000) {
            flag = 1;
            num_letradm = decena(numero / 1000).concat(" mil ").concat(miles(numero % 1000));
        }

        if (numero < 10000) {
            num_letradm = miles(numero);
        }
        return num_letradm;
    }

    private String cienmiles(long numero) {
        if (numero == 100000) {
            num_letracm = "cien mil";
        }
        if (numero >= 100000 && numero < 1000000) {
            flag = 1;
            num_letracm = centena(numero / 1000).concat(" mil ").concat(centena(numero % 1000));
        }
        if (numero < 100000) {
            num_letracm = decmiles(numero);
        }
        return num_letracm;
    }

    private String millon(long numero) {
        if (numero >= 1000000 && numero < 2000000) {
            flag = 1;
            num_letramm = ("Un millon ").concat(cienmiles(numero % 1000000));
        }
        if (numero >= 2000000 && numero < 10000000) {
            flag = 1;
            num_letramm = unidad(numero / 1000000).concat(" millones ").concat(cienmiles(numero % 1000000));
        }
        if (numero < 1000000) {
            num_letramm = cienmiles(numero);
        }
        return num_letramm;
    }

    private String decmillon(long numero) {
        if (numero == 10000000) {
            num_letradmm = "diez millones";
        }
        if (numero > 10000000 && numero < 20000000) {
            flag = 1;
            num_letradmm = decena(numero / 1000000).concat("millones ").concat(cienmiles(numero % 1000000));
        }
        if (numero >= 20000000 && numero < 100000000) {
            flag = 1;
            num_letradmm = decena(numero / 1000000).concat(" milllones ").concat(millon(numero % 1000000));
        }

        if (numero < 10000000) {
            num_letradmm = millon(numero);
        }
        return num_letradmm;
    }

    private String cienmillon(long numero) {
        if (numero == 100000000) {
            num_letradmm = "cien millones";
        }
        if (numero > 100000000 && numero < 200000000) {
            flag = 1;
            num_letradmm = centena(numero / 1000000).concat("millones ").concat(millon(numero % 1000000));
        }
        if (numero >= 200000000 && numero < 1000000000) {
            flag = 1;
            num_letradmm = centena(numero / 1000000).concat(" milllones ").concat(decmillon(numero % 1000000));
        }

        if (numero < 100000000) {
            num_letradmm = decmillon(numero);
        }
        return num_letradmm;
    }

    public String convertirLetras(long numero) {     
        num_letras = decmillon(numero);
        return num_letras;
    }
}
