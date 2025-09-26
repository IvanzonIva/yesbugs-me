package api.requests.skelethon.interfaces;

import api.models.BaseModel;

public interface CrudEndpointinterface {
    Object post(BaseModel model);
    Object get();
    Object update(BaseModel model);
    Object delete(long id);
}
