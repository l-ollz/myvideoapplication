import { IVlog } from 'app/shared/model/vlog.model';
import { ITag } from 'app/shared/model/tag.model';

export interface IPost {
  id?: number;
  title?: string;
  content?: string;
  fileContentType?: string | null;
  file?: string | null;
  vlog?: IVlog | null;
  tags?: ITag[] | null;
}

export const defaultValue: Readonly<IPost> = {};
