package requests.skelethon.interfaces;

import models.BaseModel;

public interface CrudEndpointinterface {
    Object post(BaseModel model);
    Object get();
    Object update(BaseModel model);
    Object delete(long id);
}
