import { IPost } from 'app/shared/model/post.model';

export interface ITag {
  id?: number;
  name?: string;
  logs?: IPost[] | null;
}

export const defaultValue: Readonly<ITag> = {};
