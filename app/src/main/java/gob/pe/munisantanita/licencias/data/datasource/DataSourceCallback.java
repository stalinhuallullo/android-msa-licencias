package gob.pe.munisantanita.licencias.data.datasource;

public interface DataSourceCallback {
    void onSuccess(Object data);
    void onError(String error);
}
